import dev.xdpxi.xdlib.config.Comment;
import dev.xdpxi.xdlib.config.Config;
import dev.xdpxi.xdlib.config.ConfigData;
import dev.xdpxi.xdlib.config.ConfigManager;

@Config(name = "mymod")
class MyModConfig implements ConfigData {
    boolean toggleA = true;
    boolean toggleB = false;
    String stringA = "";
    String stringB = "this is default stuff";

    @Comment("This is a comment")
    int integerA = 41;
}

public class test {
    public static void main(String[] args) throws Exception {
        // Load config from config/mymod.yml (creates it with defaults on first run)
        MyModConfig cfg = ConfigManager.load(MyModConfig.class);

        // Read values directly from fields
        System.out.println(cfg.toggleA);   // true
        System.out.println(cfg.toggleB);   // false
        System.out.println(cfg.stringA);   // ""
        System.out.println(cfg.stringB);   // "this is default stuff"
        System.out.println(cfg.integerA);  // 41

        // Modify and save back to disk
        cfg.toggleA = false;
        cfg.integerA = 100;
        ConfigManager.save(cfg);
    }
}
