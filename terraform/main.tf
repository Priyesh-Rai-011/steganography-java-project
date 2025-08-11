# AWS ECR
resource "aws_ecr_repository" "app_ecr_repo" {
  name = var.project_name
  tags = {
    Project = var.project_name
  }
}

# AWS ECS Cluster
resource "aws_ecs_cluster" "main_cluster" {
  name = "${var.project_name}-cluster"
  tags = {
    Project = var.project_name
  }
}

# AWS IAM Role for ECS Task Execution
resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.project_name}-ecs-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
    }]
  })

  tags = {
    Project = var.project_name
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}
