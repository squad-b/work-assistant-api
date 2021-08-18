package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ReservationRequestDto {

    private Long bookId;

    public void validateNotNull() {
        if (bookId == null) {
            throw new ReservationException(ReservationErrorCode.REQUIRED_RESERVATION);
        }
    }
}
