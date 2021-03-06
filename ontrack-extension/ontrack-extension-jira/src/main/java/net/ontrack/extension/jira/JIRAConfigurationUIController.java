package net.ontrack.extension.jira;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationDeletion;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ui/extension/jira/configuration")
public class JIRAConfigurationUIController extends AbstractUIController {

    private final JIRAConfigurationService jiraConfigurationService;

    @Autowired
    public JIRAConfigurationUIController(ErrorHandler errorHandler, Strings strings, JIRAConfigurationService jiraConfigurationService) {
        super(errorHandler, strings);
        this.jiraConfigurationService = jiraConfigurationService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<JIRAConfiguration> getAllConfigurations() {
        return jiraConfigurationService.getAllConfigurations();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public JIRAConfiguration createConfiguration(@RequestBody JIRAConfigurationForm configuration) {
        return jiraConfigurationService.createConfiguration(configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public JIRAConfiguration updateConfiguration(@PathVariable int id, @RequestBody JIRAConfigurationForm configuration) {
        return jiraConfigurationService.updateConfiguration(id, configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JIRAConfiguration getConfigurationById(@PathVariable int id) {
        return jiraConfigurationService.getConfigurationById(id);
    }

    @RequestMapping(value = "/{id}/deletion", method = RequestMethod.GET)
    @ResponseBody
    public JIRAConfigurationDeletion getConfigurationForDeletion(@PathVariable int id) {
        return jiraConfigurationService.getConfigurationForDeletion(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Ack deleteConfiguration(@PathVariable int id) {
        return jiraConfigurationService.deleteConfiguration(id);
    }
}
