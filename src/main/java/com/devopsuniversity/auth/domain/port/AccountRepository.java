package com.devopsuniversity.auth.domain.port;

import com.devopsuniversity.auth.domain.model.Account;
import com.devopsuniversity.auth.domain.model.Username;
import java.util.Optional;

/** Puerto de salida: almacén de cuentas. */
public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByUsername(Username username);

    boolean existsByUsername(Username username);
}
