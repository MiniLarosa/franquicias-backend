output "mongo_container_name" {
  description = "Nombre del contenedor MongoDB creado por Terraform."
  value       = docker_container.mongo.name
}

output "mongo_host_port" {
  description = "Puerto publicado para MongoDB."
  value       = var.mongo_host_port
}

output "mongo_connection_uri_local" {
  description = "URI de conexion local para la aplicacion."
  value       = "mongodb://${var.mongo_root_username}:${var.mongo_root_password}@localhost:${var.mongo_host_port}/${var.mongo_database}?authSource=admin"
  sensitive   = true
}
