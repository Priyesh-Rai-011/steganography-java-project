# GENERAL CONFIGURATION
variable "aws_region" {
  description = "The AWS region where resources will be created."
  type        = string
  default     = "ap-south-1" # Change this to your company's preferred region
}

variable "project_name" {
  description = "The name of the project, used for naming resources."
  type        = string
  default     = "stego-secure"
}

# DOCKER IMAGE CONFIGURATION

variable "docker_image_uri" {
  description = "The full URI of the Docker image to deploy (e.g., from Docker Hub)."
  type        = string
  # IMPORTANT: Update this line with your new image URI
  default     = "priyeshrai711/stegosecure-app:v2"
}

# ECS FARGATE CONFIGURATION
variable "container_port" {
  description = "The port number the application container listens on."
  type        = number
  default     = 8080
}

variable "fargate_cpu" {
  description = "Fargate instance CPU units to provision (e.g., 256 = 0.25 vCPU)."
  type        = number
  default     = 256
}

variable "fargate_memory" {
  description = "Fargate instance memory to provision (in MiB)."
  type        = number
  default     = 512
}
