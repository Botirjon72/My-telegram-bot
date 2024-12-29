package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Namoz {
    private String region;
    private String date;
    private String weekday;
    private hijriy_date hijriy_date;
    private times times;
}
