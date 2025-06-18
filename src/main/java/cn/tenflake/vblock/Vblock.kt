package cn.tenflake.vblock

import cn.tenflake.vblock.command.SavePlayerData
import cn.tenflake.vblock.command.Visible
import com.comphenix.protocol.ProtocolLibrary
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Vblock : JavaPlugin(), Listener, CommandExecutor {
    override fun onEnable() {
        val protocol = ProtocolLibrary.getProtocolManager()
        this.getCommand("visible").executor = Visible()
        this.getCommand("visibleblocks").executor = Visible()
        this.getCommand("savedata").executor = SavePlayerData()
    }

    override fun onDisable() {

    }
}
