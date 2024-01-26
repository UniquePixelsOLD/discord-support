package net.uniquepixels.support.api;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public enum Roles {

    ADMIN("1188248880941371432"),
    STAFF("1195372156062797844");

    private final String roleId;

    Roles(String roleId) {
        this.roleId = roleId;
    }

    public static boolean hasMemberRole(@NotNull Member member, @NotNull Roles... roles) {

        Guild guild = member.getGuild();

        AtomicBoolean bool = new AtomicBoolean(false);

        for (Roles role : roles) {

            Role roleById = guild.getRoleById(role.roleId);
            if (member.getRoles().contains(roleById))
                bool.set(true);


        }


        return bool.get();
    }

    public String getRoleId() {
        return this.roleId;
    }
}
