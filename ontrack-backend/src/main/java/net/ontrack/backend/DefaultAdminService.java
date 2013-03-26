package net.ontrack.backend;

import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.configuration.ConfigurationExtensionService;
import net.ontrack.service.AdminService;
import net.ontrack.service.EventService;
import net.ontrack.service.model.LDAPConfiguration;
import net.ontrack.service.model.MailConfiguration;
import net.ontrack.service.validation.LDAPConfigurationValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DefaultAdminService extends AbstractServiceImpl implements AdminService {

    private final ConfigurationService configurationService;
    private final ConfigurationExtensionService configurationExtensionService;

    private final AtomicInteger ldapConfigurationSequence = new AtomicInteger(0);

    @Autowired
    public DefaultAdminService(Validator validator, EventService eventService, ConfigurationService configurationService, ConfigurationExtensionService configurationExtensionService) {
        super(validator, eventService);
        this.configurationService = configurationService;
        this.configurationExtensionService = configurationExtensionService;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = Caches.CONFIGURATION, key = "ldap")
    public LDAPConfiguration getLDAPConfiguration() {
        LDAPConfiguration c = new LDAPConfiguration();
        boolean enabled = configurationService.getBoolean(ConfigurationKey.LDAP_ENABLED, false, false);
        c.setEnabled(enabled);
        if (enabled) {
            c.setHost(configurationService.get(ConfigurationKey.LDAP_HOST, true, null));
            c.setPort(configurationService.getInteger(ConfigurationKey.LDAP_PORT, true, 0));
            c.setSearchBase(configurationService.get(ConfigurationKey.LDAP_SEARCH_BASE, true, null));
            c.setSearchFilter(configurationService.get(ConfigurationKey.LDAP_SEARCH_FILTER, true, null));
            c.setUser(configurationService.get(ConfigurationKey.LDAP_USER, true, null));
            c.setPassword(configurationService.get(ConfigurationKey.LDAP_PASSWORD, true, null));
        } else {
            // Default values
            c.setPort(389);
        }
        // OK
        c.setSequence(ldapConfigurationSequence.incrementAndGet());
        return c;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = Caches.CONFIGURATION, key = "mail")
    public MailConfiguration getMailConfiguration() {
        MailConfiguration c = new MailConfiguration();
        c.setHost(configurationService.get(ConfigurationKey.MAIL_HOST, false, null));
        c.setReplyToAddress(configurationService.get(ConfigurationKey.MAIL_REPLY_TO_ADDRESS, false, null));
        c.setAuthentication(configurationService.getBoolean(ConfigurationKey.MAIL_AUTHENTICATION, false, false));
        c.setStartTls(configurationService.getBoolean(ConfigurationKey.MAIL_START_TLS, false, false));
        c.setUser(configurationService.get(ConfigurationKey.MAIL_USER, false, null));
        c.setPassword(configurationService.get(ConfigurationKey.MAIL_PASSWORD, false, null));
        return c;
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    @Caching(evict = {
            @CacheEvict(value = Caches.CONFIGURATION, key = "ldap"),
            @CacheEvict(value = Caches.LDAP, key = "0")
    })

    public void saveLDAPConfiguration(LDAPConfiguration configuration) {
        // Validation
        validate(configuration, LDAPConfigurationValidation.class);
        // Saving...
        configurationService.set(ConfigurationKey.LDAP_ENABLED, configuration.isEnabled());
        if (configuration.isEnabled()) {
            configurationService.set(ConfigurationKey.LDAP_HOST, configuration.getHost());
            configurationService.set(ConfigurationKey.LDAP_PORT, configuration.getPort());
            configurationService.set(ConfigurationKey.LDAP_SEARCH_BASE, configuration.getSearchBase());
            configurationService.set(ConfigurationKey.LDAP_SEARCH_FILTER, configuration.getSearchFilter());
            configurationService.set(ConfigurationKey.LDAP_USER, configuration.getUser());
            configurationService.set(ConfigurationKey.LDAP_PASSWORD, configuration.getPassword());
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    @Caching(evict = {
            @CacheEvict(value = Caches.CONFIGURATION, key = "mail"),
            @CacheEvict(value = Caches.MAIL, key = "0")
    })

    public void saveMailConfiguration(MailConfiguration configuration) {
        configurationService.set(ConfigurationKey.MAIL_HOST, configuration.getHost());
        configurationService.set(ConfigurationKey.MAIL_REPLY_TO_ADDRESS, configuration.getReplyToAddress());
        configurationService.set(ConfigurationKey.MAIL_USER, configuration.getUser());
        configurationService.set(ConfigurationKey.MAIL_PASSWORD, configuration.getPassword());
        configurationService.set(ConfigurationKey.MAIL_AUTHENTICATION, configuration.isAuthentication());
        configurationService.set(ConfigurationKey.MAIL_START_TLS, configuration.isStartTls());
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public String saveExtensionConfiguration(String extension, String name, Map<String, String> parameters) {
        return configurationExtensionService.saveExtensionConfiguration(extension, name, parameters);
    }
}
