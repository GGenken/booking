package middleware

import (
	"context"
	"database/sql"
	"net/http"

	"gorm.io/gorm"
)

type contextKey string

const dbKey contextKey = "db"

func WithDB(db *gorm.DB) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			ctx := context.WithValue(r.Context(), dbKey, db)
			next.ServeHTTP(w, r.WithContext(ctx))
		})
	}
}

func GetDB(r *http.Request) *gorm.DB {
	if db, ok := r.Context().Value(dbKey).(*gorm.DB); ok {
		if db == nil {
			panic("DB is nil")
		}
		return db
	}
	panic("Database is unavailable") // TODO: create a recovery middleware
}

func GetPool(r *http.Request) *sql.DB {
	db, _ := GetDB(r).DB()
	return db
}
