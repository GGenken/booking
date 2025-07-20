package handlers

import (
	"auth/internal/helpers"
	"auth/internal/middleware"
	"auth/internal/models"
	"auth/internal/models/dto"
	"encoding/json"
	"golang.org/x/crypto/bcrypt"
	"log"
	"net/http"
)

func RegisterHandler() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var userDTO dto.InputUserDTO
		if err := json.NewDecoder(r.Body).Decode(&userDTO); err != nil {
			helpers.SendErrorResponse(w, http.StatusBadRequest, err.Error())
			return
		}

		if err := userDTO.Validate(); err != nil {
			helpers.SendErrorResponse(w, http.StatusBadRequest, err.Error())
			return
		}

		hash, err := bcrypt.GenerateFromPassword([]byte(userDTO.Password), bcrypt.DefaultCost)
		if err != nil {
			log.Printf("Error hashing password: %v", err)
			helpers.SendErrorResponse(w, http.StatusInternalServerError, "Internal server error")
			return
		}

		user := models.User{
			Username: userDTO.Username,
			Password: string(hash),
		}

		db := middleware.GetDB(r)

		var existingUser models.User
		if err := db.Where("username = ?", user.Username).First(&existingUser).Error; err == nil {
			helpers.SendErrorResponse(w, http.StatusConflict, "Username taken")
			return
		}

		if err := db.Create(&user).Error; err != nil {
			helpers.SendErrorResponse(w, http.StatusInternalServerError, "Internal server error")
			return
		}

		w.WriteHeader(http.StatusCreated)
		json.NewEncoder(w).Encode(map[string]interface{}{
			"uuid":     user.UUID,
			"role":     user.Role,
			"username": user.Username,
		})
	}
}
