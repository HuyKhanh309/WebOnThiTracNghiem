package com.example.WebOnThiTracNghiem.DTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentrestDTO implements Serializable {
    private String status;
    private String message;
    private String URL;

}
