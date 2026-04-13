variable "mongo_container_name" {
  type        = string
  default     = "franquicias-mongodb-iac"
  description = "Nombre del contenedor MongoDB aprovisionado por Terraform."
}

variable "mongo_volume_name" {
  type        = string
  default     = "franquicias_mongo_data_iac"
  description = "Nombre del volumen Docker para persistencia de MongoDB."
}

variable "mongo_network_name" {
  type        = string
  default     = "franquicias-iac-net"
  description = "Nombre de la red Docker para MongoDB aprovisionada por Terraform."
}

variable "mongo_root_username" {
  type        = string
  default     = "admin"
  description = "Usuario root de MongoDB."
}

variable "mongo_root_password" {
  type        = string
  default     = "admin"
  description = "Contrasena root de MongoDB."
  sensitive   = true
}

variable "mongo_database" {
  type        = string
  default     = "franquiciasdb"
  description = "Base de datos inicial de MongoDB."
}

variable "mongo_host_port" {
  type        = number
  default     = 27017
  description = "Puerto host para publicar MongoDB."
}
