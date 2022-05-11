package com.frauas.exercisegenerator.documents;

import com.frauas.exercisegenerator.enums.SolutionType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Solution {
    @Getter
    private final SolutionType type;
}
