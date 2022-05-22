package com.frauas.exercisegenerator.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class CreateUserDto
{
	@NotNull
	private String username;

	@NotNull
	private String password;
}
