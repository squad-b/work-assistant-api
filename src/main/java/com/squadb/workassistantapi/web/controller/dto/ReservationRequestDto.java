package com.squadb.workassistantapi.web.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ReservationRequestDto {

    private long bookId;
}
