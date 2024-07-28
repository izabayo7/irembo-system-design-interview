package rw.companyz.useraccountms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public abstract class BaseController {
    @Autowired
    private MessageSource messageSource;

    protected abstract String getEntityName();

    String localize(String path) {
        Object[] args = {2};
        args[0] = getEntityName();
        return  messageSource.getMessage(path, args, LocaleContextHolder.getLocale());
    }
}
