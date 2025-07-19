package models

type User struct {
	UID      uint   `gorm:"primaryKey;autoIncrement"`
	UUID     string `gorm:"type:uuid;default:uuid_generate_v4();unique;not null"`
	Username string `gorm:"unique;not null"`
	Password string `gorm:"not null"`
	Role     string `gorm:"type:text;not null;default:user"`
}
