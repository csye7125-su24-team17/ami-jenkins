#!/bin/bash

# Set debconf to run in non-interactive mode
export DEBIAN_FRONTEND=noninteractive

# This script installs, configures and starts Jenkins on the AMI

##########################################################################
## Installing Jenkins and other dependencies

# Update package information
sudo apt-get update -y

# Install Java (Required by Jenkins) and Maven
sudo apt-get install -y openjdk-11-jdk maven

# Download the Jenkins repository key and saves it to /usr/share/keyrings/jenkins-keyring.asc,
# which is used to authenticate packages
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key

# Add the Jenkins repository to the packages sources list, specifying that packages from
# this repository should be verified using the key saved in /usr/share/keyrings/jenkins-keyring.asc.
echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Update the package lists to include newly available packages from the added Jenkins repository.
sudo apt-get update

# Install the Jenkins package from the newly added repository
sudo apt-get install jenkins -y

sleep 3

# Check the status of Jenkins service
sudo systemctl --full status jenkins

# Check Jenkins version
echo "Jenkins $(jenkins --version)"

##########################################################################
## Installing Helm

curl https://baltocdn.com/helm/signing.asc | gpg --dearmor | sudo tee /usr/share/keyrings/helm.gpg > /dev/null
sudo apt-get install apt-transport-https --yes
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/helm.gpg] https://baltocdn.com/helm/stable/debian/ all main" | sudo tee /etc/apt/sources.list.d/helm-stable-debian.list
sudo apt-get update
sudo apt-get install helm -y

##########################################################################
## Installing Node.js, npm, and global npm packages

# Add NodeSource GPG key and setup repository
echo "Adding NodeSource GPG key and setting up repository..."
curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | sudo gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg
NODE_MAJOR=20
echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | sudo tee /etc/apt/sources.list.d/nodesource.list

# Update the package list to include Node.js packages
echo "Updating package list for Node.js..."
sudo apt-get update -y

# Install Node.js
echo "Installing Node.js..."
sudo apt-get install -y nodejs

# Verify Node.js and npm installation
echo "Verifying Node.js and npm installation..."
node --version
npm --version

# Install necessary global npm packages
echo "Installing global npm packages..."
sudo npm install -g semantic-release@latest
sudo npm install -g @semantic-release/git@latest
sudo npm install -g @semantic-release/exec@latest
sudo npm install -g conventional-changelog-conventionalcommits
sudo npm install -g npm-cli-login

##########################################################################
## Installing GitHub CLI

echo "Installing GitHub CLI..."
sudo apt-get install -y gh

# Confirm all installations
echo "Installation complete. Versions:"
echo "Node.js: $(node --version)"
echo "npm: $(npm --version)"
echo "semantic-release: $(semantic-release --version)"
echo "GitHub CLI: $(gh --version)"

##########################################################################
## Installing Docker Buildx

echo "Installing Docker Buildx..."
sudo mkdir -p ~/.docker/cli-plugins/
curl -sL https://github.com/docker/buildx/releases/download/v0.14.1/buildx-v0.14.1.linux-amd64 -o ~/.docker/cli-plugins/docker-buildx
chmod +x ~/.docker/cli-plugins/docker-buildx
export PATH=$PATH:~/.docker/cli-plugins


#########################################################################
## Caddy(stable) installation docs: https://caddyserver.com/docs/install#debian-ubuntu-raspbian

# Install and configure keyring for caddy stable release:
sudo apt-get install -y debian-keyring debian-archive-keyring apt-transport-https
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo \
  gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee \
  /etc/apt/sources.list.d/caddy-stable.list

# Install caddy:
sudo apt-get update && sudo apt-get install caddy -y

# Enable Caddy service
sudo systemctl enable caddy

# Remove default Caddyfile
sudo rm /etc/caddy/Caddyfile

# Create new Caddyfile for Jenkins
sudo tee /etc/caddy/Caddyfile <<EOF
{
    acme_ca https://acme-staging-v02.api.letsencrypt.org/directory
}
jenkins.hemanthnvd.com {
  reverse_proxy http://127.0.0.1:8080
}
EOF


# Restart Caddy service to apply new configuration
sudo systemctl restart caddy

##########################################################################
## Installing Docker

# Add Docker's official GPG key:
sudo apt-get install ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# Add the repository to Apt sources:
echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" |
  sudo tee /etc/apt/sources.list.d/docker.list >/dev/null

# Install Docker:
sudo apt-get update && sudo apt-get install docker-ce -y

# Provide relevant permissions
sudo chmod 666 /var/run/docker.sock
sudo usermod -a -G docker jenkins

# Check Docker version
echo "Docker $(docker --version)"

##########################################################################
## Installing Plugins for Jenkins

# Install Jenkins plugin manager tool to be able to install the plugins on EC2 instance
wget --quiet \
  https://github.com/jenkinsci/plugin-installation-manager-tool/releases/download/2.12.13/jenkins-plugin-manager-2.12.13.jar

# Install plugins with jenkins-plugin-manager tool:
sudo java -jar ./jenkins-plugin-manager-2.12.13.jar --war /usr/share/java/jenkins.war \
  --plugin-download-directory /var/lib/jenkins/plugins --plugin-file /home/ubuntu/plugins-list.txt

# Copy Jenkins config file to Jenkins home
sudo cp /home/ubuntu/jenkins.yaml /var/lib/jenkins/

# Make jenkins user and group owner of jenkins.yaml file
sudo chown jenkins:jenkins /var/lib/jenkins/jenkins.yaml

# Copy Jenkins DSL Job scripts to Jenkins home
sudo cp /home/ubuntu/build-and-push-static-site.groovy /var/lib/jenkins/
sudo cp /home/ubuntu/conventional-commit.groovy /var/lib/jenkins/
# sudo cp /home/ubuntu/webapp.groovy /var/lib/jenkins/

# Make jenkins user and group owner of Jenkins DSL job
sudo chown jenkins:jenkins /var/lib/jenkins/build-and-push-static-site.groovy
sudo chown jenkins:jenkins /var/lib/jenkins/conventional-commit.groovy
# sudo chown jenkins:jenkins /var/lib/jenkins/webapp.groovy

# Update users and group permissions to `jenkins` for all installed plugins:
cd /var/lib/jenkins/plugins/ || exit
sudo chown jenkins:jenkins ./*

# Configure JAVA_OPTS to disable setup wizard
sudo mkdir -p /etc/systemd/system/jenkins.service.d/
{
  echo "[Service]"
  echo "Environment=\"JAVA_OPTS=-Djava.awt.headless=true -Djenkins.install.runSetupWizard=false -Dcasc.jenkins.config=/var/lib/jenkins/jenkins.yaml\""
} | sudo tee /etc/systemd/system/jenkins.service.d/override.conf

# Restart jenkins service
sudo systemctl daemon-reload
sudo systemctl stop jenkins
sudo systemctl start jenkins