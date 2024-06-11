# Jenkins AMI with Packer

- Used Ubuntu 24.04 LTS as the source image  
- AMI has Jenkins fully configured starting from plugin installation to automated user creation via Jenkins Configuration as Code  
- Used Caddy as reverse proxy for Jenkins instance and set them up to get SSL certificate from Let's Encrypt on startup.
- This Packer AMI is built via GitHub Actions workflow which also format checks the packer template

## Packer configuration
- Builds image in default AWS VPC with OS - Ubuntu, Volume Size - 8GB, Device Name - gp2
- Installs dependencies via shell script

## Packer commands:  
- `packer init`: Installs all packer plugins mentioned in the config template
- `packer fmt`: Formats template
- `packer validate`: Validates the template
- `packer build <filename>.pkr.hcl`: Builds the custom AMI

---testing
--testing
