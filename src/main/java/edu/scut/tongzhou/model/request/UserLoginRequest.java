package edu.scut.tongzhou.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author DylanS
 * @version 1.0
 */
@Data
public class UserLoginRequest implements Serializable {
    private String userAccount;

    private String userPassword;

    @Serial
    private static final long serialVersionUID = -8765050689861040016L;
}
