package dto

type UserResponseDTO struct {
	UUID     string `json:"uid"`
	Username string `json:"username"`
	Role     string `json:"role"`
}
