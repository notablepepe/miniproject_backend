package iss.project_springboot.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmailDetailsWithFile {

    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;

}
