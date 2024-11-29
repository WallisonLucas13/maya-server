package com.example.ia.mayaAI.converters;

import com.example.ia.mayaAI.inputs.UserInput;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.utils.UuidGenerator;

public class UserConverter {

    public static UserModel convert(UserInput userInput) {
        UserModel user = new UserModel();
        user.setId(UuidGenerator.generate().toString());
        user.setUsername(userInput.getUsername());
        user.setPassword(userInput.getPassword());
        return user;
    }
}
