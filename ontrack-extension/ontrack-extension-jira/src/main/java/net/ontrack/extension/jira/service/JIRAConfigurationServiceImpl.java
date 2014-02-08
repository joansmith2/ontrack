package net.ontrack.extension.jira.service;

import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.dao.JIRAConfigurationDao;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JIRAConfigurationServiceImpl implements JIRAConfigurationService {

    private final JIRAConfigurationDao jiraConfigurationDao;

    @Autowired
    public JIRAConfigurationServiceImpl(JIRAConfigurationDao jiraConfigurationDao) {
        this.jiraConfigurationDao = jiraConfigurationDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JIRAConfiguration> getAllConfigurations() {
        return jiraConfigurationDao.findAll();
    }
}
