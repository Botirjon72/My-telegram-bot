package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Collation {
    private int id;
    private String Code;
    private String Ccy;
    private String CcyNm_UZ;
    private String Rate;
    private String Date;
    private String Diff;
}
