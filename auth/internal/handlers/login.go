package handlers

import (
	"auth/internal/helpers"
	"auth/internal/middleware"
	"auth/internal/models"
	"auth/internal/models/dto"
	"encoding/json"
	"errors"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
	"net/http"
)

func LoginHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var userDTO dto.InputUserDTO
		if err := json.NewDecoder(r.Body).Decode(&userDTO); err != nil {
			http.Error(w, "Invalid input", http.StatusBadRequest)
			return
		}

		if err := userDTO.Validate(); err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		var user models.User
		db := middleware.GetDB(r)

		if err := db.Where("username = ?", userDTO.Username).First(&user).Error; err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				http.Error(w, "Invalid credentials", http.StatusUnauthorized)
				return
			}
			http.Error(w, "Internal server error", http.StatusInternalServerError)
			return
		}

		if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(userDTO.Password)); err != nil {
			http.Error(w, "Invalid credentials", http.StatusUnauthorized)
			return
		}

		userResponseDTO := dto.UserResponseDTO{
			Username: user.Username,
			Role:     user.Role,
			UUID:     user.UUID,
		}

		token, err := helpers.CreateJWT(user.UUID, user.Username, user.Role)
		if err != nil {
			http.Error(w, "Error generating JWT", http.StatusInternalServerError)
			return
		}

		response := map[string]interface{}{
			"user":  userResponseDTO,
			"token": token,
		}

		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(response)
	}
}
