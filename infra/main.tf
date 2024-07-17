terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.40.0"
    }
  }

  required_version = ">= 1.0.0"
}

data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

#ECS Setting
resource "aws_ecs_cluster" "hackathon_doctor_cluster" {
  name = "hackathon-doctor-cluster"
}
#ECS Setting

#DB Setting
resource "aws_db_instance" "db_hackathon_doctor" {
  identifier           = "hackaton-doctor-db"
  engine               = "mysql"
  engine_version       = "8.0"
  instance_class       = "db.t3.micro"
  allocated_storage    = 10
  db_name              = "hackaton-doctor-db"
  username             = "hackathon_doctor"
  password             = "db_hackathon_doctor-password"
  parameter_group_name = "default.mysql8.0"
  skip_final_snapshot  = true
  port                 = 3306
}
#DB Setting

#ECS Setting
locals {
  split_endpoint = element(split(":", aws_db_instance.db_hackathon_doctor.endpoint), 0)
}

resource "aws_ecs_task_definition" "hackathon_doctor_task" {
  family                   = "hackathon-doctor-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = jsonencode(
    [
      {
        name      = "hackathon-doctor-container"
        image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${data.aws_region.current.name}.amazonaws.com/hackathon-doctor:latest"
        cpu : 512,
        memory : 1024,
        essential : true,
        portMappings = [
          {
            containerPort = 80
            hostPort      = 80
            protocol      = "tcp"
          },
          {
            containerPort = 8080
            hostPort      = 8080
            protocol      = "tcp"
          }
        ],
        environment = [
          { name = "PORT", value = "8080" },
          { name = "DB_NAME", value = aws_db_instance.db_hackathon_doctor.db_name },
          { name = "DB_HOST", value = local.split_endpoint },
          { name = "DB_PORT", value = "${tostring(aws_db_instance.db_hackathon_doctor.port)}" },
          { name = "DB_USERNAME", value = aws_db_instance.db_hackathon_doctor.username },
          { name = "DB_PASSWORD", value = "${aws_db_instance.db_hackathon_doctor.username}-password" },
          { name = "NODE_ENV", value = "dev" },

        ]
      }
    ]
  )
}

resource "aws_iam_role_policy_attachment" "ecs_execution_policy_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "ecs_execution_role"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

#ECS Setting

# ECS SERVICE
resource "aws_ecs_service" "hackathon_doctor_service" {
  name            = "hackathon-doctor-service"
  cluster         = aws_ecs_cluster.hackathon_doctor_cluster.id
  task_definition = aws_ecs_task_definition.hackathon_doctor_task.arn
  launch_type     = "FARGATE"
  desired_count   =  2

  network_configuration {
    subnets          = ["subnet-0de1ca34acd9dea27"]
    security_groups  = [aws_security_group.hackathon_doctor_sg.id]
    assign_public_ip = true
  }
}

resource "aws_security_group" "hackathon_doctor_sg" {
  name        = "hackathon-doctor-sg"
  description = "Allow all inbound traffic"
  vpc_id      = "vpc-0bd1f59a1daae83c1"  # Consider dynamic retrieval

  ingress {
    description      = "Allow all inbound traffic"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  egress {
    description      = "Allow all outbound traffic"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }
}
