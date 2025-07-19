package dto

import (
	"errors"
	"regexp"
)

type InputUserDTO struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

func (u *InputUserDTO) Validate() error {
	if u.Username == "" {
		return errors.New("username is required")
	}
	match, _ := regexp.MatchString("^[a-zA-Z0-9]{2,16}$", u.Username)
	if !match {
		return errors.New("username must be alphanumeric and between 2 to 16 characters long")
	}

	if u.Password == "" {
		return errors.New("password is required")
	}
	match, _ = regexp.MatchString("^[a-zA-Z0-9!@#$%^&*()_+=]{4,128}$", u.Password)
	if !match {
		return errors.New("password must be alphanumeric with \"!@#$%^&*()_+=\" and between 4 to 128 characters long")
	}

	return nil
}
