packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = "~> 1"
    }
  }
}
variable "aws_region" {
  type        = string
  description = "AWS Region"
  default     = "us-east-1"
}

variable "source_ami" {
  type        = string
  description = "Base Ubuntu image to build our custom AMI"
  default     = "ami-04b70fa74e45c3917" # Ubuntu 24.04 LTS
}

variable "ami_prefix" {
  type        = string
  description = "AWS AMI name prefix"
  default     = "csye-7125"
}

variable "ssh_username" {
  type        = string
  description = "username to ssh into the AMI Instance"
  default     = "ubuntu"
}

variable "subnet_id" {
  type        = string
  description = "Subnet of the default VPC"
}

variable "ami_users" {
  type        = list(string)
  description = "List of account IDs that will have access the custom AMI"
}

variable "OS" {
  type        = string
  description = "Base operating system version"
  default     = "Ubuntu"
}

variable "ubuntu_version" {
  type        = string
  description = "Version of the custom AMI"
  default     = "24.04 LTS"
}

variable "instance_type" {
  type        = string
  description = "AWS AMI instance type"
  default     = "t2.medium"
}

variable "volume_type" {
  type        = string
  description = "EBS volume type"
  default     = "gp2"
}

variable "volume_size" {
  type        = string
  description = "EBS volume size"
  default     = "50"
}

variable "device_name" {
  type        = string
  description = "EBS device name"
  default     = "/dev/sda1"
}

locals {
  timestamp = substr(regex_replace(timestamp(), "[- TZ:]", ""), 8, 13)
}

source "amazon-ebs" "ubuntu" {
  region          = "${var.aws_region}"
  ami_name        = "${var.ami_prefix}-${var.ubuntu_version}-${local.timestamp}"
  ami_description = "Ubuntu AMI for CSYE 7125"
  tags = {
    Name         = "${var.ami_prefix}-${local.timestamp}"
    Base_AMI_ID  = "${var.source_ami}"
    TimeStamp_ID = "${local.timestamp}"
    OS_Version   = "${var.OS}"
    Release      = "${var.ubuntu_version}"
  }
  ami_regions = [
    "${var.aws_region}",
  ]

  instance_type = "${var.instance_type}"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"
  ami_users     = "${var.ami_users}"

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "${var.device_name}"
    volume_size           = "${var.volume_size}"
    volume_type           = "${var.volume_type}"
  }
}

build {
  sources = ["source.amazon-ebs.ubuntu"]

  provisioner "file" {
    source      = "./jenkins/plugins.txt"
    destination = "/home/ubuntu/plugins.txt"
  }

  provisioner "shell" {
    name = "Installs Jenkins and all its dependencies, starts the service"
    scripts = [
      "packer/scripts/install.sh"
    ]
  }
}