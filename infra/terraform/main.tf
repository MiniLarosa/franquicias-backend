resource "docker_image" "mongo" {
  name         = "mongo:7"
  keep_locally = true
}

resource "docker_network" "mongo_network" {
  name = var.mongo_network_name
}

resource "docker_volume" "mongo_data" {
  name = var.mongo_volume_name
}

resource "docker_container" "mongo" {
  name  = var.mongo_container_name
  image = docker_image.mongo.image_id

  restart = "unless-stopped"

  env = [
    "MONGO_INITDB_ROOT_USERNAME=${var.mongo_root_username}",
    "MONGO_INITDB_ROOT_PASSWORD=${var.mongo_root_password}",
    "MONGO_INITDB_DATABASE=${var.mongo_database}"
  ]

  networks_advanced {
    name = docker_network.mongo_network.name
  }

  ports {
    internal = 27017
    external = var.mongo_host_port
  }

  volumes {
    volume_name    = docker_volume.mongo_data.name
    container_path = "/data/db"
  }
}
