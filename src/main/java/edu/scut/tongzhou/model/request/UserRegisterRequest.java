package edu.scut.tongzhou.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author DylanS
 * @version 1.0
 */
@Data
public class UserRegisterRequest implements Serializable {
    private String userAccount;

    private String userPassword;

    private String confirmPassword;

    @Serial
    private static final long serialVersionUID = 2494470058836843507L;
}
