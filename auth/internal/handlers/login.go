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
			helpers.SendErrorResponse(w, http.StatusBadRequest, "Invalid input")
			return
		}

		if err := userDTO.Validate(); err != nil {
			helpers.SendErrorResponse(w, http.StatusBadRequest, err.Error())
			return
		}

		var user models.User
		db := middleware.GetDB(r)

		if err := db.Where("username = ?", userDTO.Username).First(&user).Error; err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				helpers.SendErrorResponse(w, http.StatusNotFound, "User not found")
				return
			}
			helpers.SendErrorResponse(w, http.StatusInternalServerError, "Internal server error")
			return
		}

		if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(userDTO.Password)); err != nil {
			helpers.SendErrorResponse(w, http.StatusUnauthorized, "Invalid credentials")
			return
		}

		userResponseDTO := dto.UserResponseDTO{
			Username: user.Username,
			Role:     user.Role,
			UUID:     user.UUID,
		}

		token, err := helpers.CreateJWT(user.UUID, user.Username, user.Role)
		if err != nil {
			helpers.SendErrorResponse(w, http.StatusInternalServerError, "Internal server error")
			return
		}

		response := map[string]interface{}{
			"user":  userResponseDTO,
			"token": token,
		}

		w.WriteHeader(http.StatusOK)
		err = json.NewEncoder(w).Encode(response)
		if err != nil {
			helpers.SendErrorResponse(w, http.StatusInternalServerError, err.Error())
			return
		}
	}
}
