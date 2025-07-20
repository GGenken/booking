package dto

type UserResponseDTO struct {
	UUID     string `json:"uuid"`
	Username string `json:"username"`
	Role     string `json:"role"`
}
