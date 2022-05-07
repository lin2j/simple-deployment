package tech.lin2j.idea.plugin.domain.model;

import com.intellij.openapi.application.ApplicationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author linjinjia
 * @date 2022/4/25 17:27
 */
public class ConfigHelper {

    private static final ConfigPersistence CONFIG_PERSISTENCE =
            ApplicationManager.getApplication().getService(ConfigPersistence.class);

    private static final Map<Integer, SshServer> SSH_SERVER_MAP;

    private static Map<Integer, List<Command>> COMMAND_MAP;

    private static Map<Integer, List<SshUpload>> UPLOAD_MAP;

    static {
        SSH_SERVER_MAP = CONFIG_PERSISTENCE.getSshServers().stream()
                .collect(Collectors.toMap(SshServer::getId, s -> s, (s1, s2) -> s1));

        COMMAND_MAP = CONFIG_PERSISTENCE.getCommands().stream()
                .collect(Collectors.groupingBy(Command::getSshId));

        UPLOAD_MAP = CONFIG_PERSISTENCE.getSshUploads().stream()
                .collect(Collectors.groupingBy(SshUpload::getSshId));
    }

    public static SshServer getSshServerById(int id) {
        return SSH_SERVER_MAP.get(id);
    }

    public static List<SshServer> sshServers() {
        return CONFIG_PERSISTENCE.getSshServers();
    }

    public static int maxSshServerId() {
        return CONFIG_PERSISTENCE.getSshServers().stream()
                .map(SshServer::getId)
                .max(Integer::compareTo).orElse(0);
    }

    public static void addSshServer(SshServer sshServer) {
        CONFIG_PERSISTENCE.getSshServers().add(sshServer);
        SSH_SERVER_MAP.put(sshServer.getId(), sshServer);
    }

    public static void removeSshServer(SshServer sshServer) {
        CONFIG_PERSISTENCE.getSshServers().remove(sshServer);
        SSH_SERVER_MAP.remove(sshServer.getId());
    }

    public static void removeSshServer(Integer id) {
        SshServer sshServer = SSH_SERVER_MAP.get(id);
        if (sshServer == null) {
            return;
        }
        removeSshServer(sshServer);
    }

    public static List<Command> getCommandsBySshId(int sshId) {
        return COMMAND_MAP.getOrDefault(sshId, new ArrayList<>());
    }

    public static void addCommand(Command command) {
        CONFIG_PERSISTENCE.getCommands().add(command);
        COMMAND_MAP = CONFIG_PERSISTENCE.getCommands().stream()
                .collect(Collectors.groupingBy(Command::getSshId));
    }

    public static void removeCommand(Command command) {
        CONFIG_PERSISTENCE.getCommands().remove(command);
        COMMAND_MAP = CONFIG_PERSISTENCE.getCommands().stream()
                .collect(Collectors.groupingBy(Command::getSshId));
    }

    public static Integer maxCommandId() {
        return CONFIG_PERSISTENCE.getCommands().stream()
                .map(Command::getId)
                .max(Integer::compareTo).orElse(0);
    }

    public static Command getCommandById(int id) {
        return CONFIG_PERSISTENCE.getCommands().stream()
                .filter(command -> command.getId() == id)
                .findFirst().orElse(null);
    }

    public static boolean addSshUpload(SshUpload sshUpload) {
        for (SshUpload upload : CONFIG_PERSISTENCE.getSshUploads()) {
            if (upload.equals(sshUpload)) {
                upload.setSelected(true);
                return false;
            }
        }
        CONFIG_PERSISTENCE.getSshUploads().add(sshUpload);
        UPLOAD_MAP = CONFIG_PERSISTENCE.getSshUploads().stream()
                .collect(Collectors.groupingBy(SshUpload::getSshId));
        sshUpload.setSelected(true);
        return true;
    }

    public static List<SshUpload> getSshUploadsBySshId(int sshId) {
        return UPLOAD_MAP.getOrDefault(sshId, new ArrayList<>());
    }
}