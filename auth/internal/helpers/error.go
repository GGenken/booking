package helpers

import (
	"encoding/json"
	"net/http"
)

type CustomErrorResponse struct {
	Error string `json:"error"`
}

func SendErrorResponse(w http.ResponseWriter, statusCode int, errorMessage string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(statusCode)
	json.NewEncoder(w).Encode(CustomErrorResponse{Error: errorMessage})
}
