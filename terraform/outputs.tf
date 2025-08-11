output "application_url" {
  description = "The public URL of the deployed application."
  value       = "http://${aws_lb.main_alb.dns_name}"
}