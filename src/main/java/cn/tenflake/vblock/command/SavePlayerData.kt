package cn.tenflake.vblock.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SavePlayerData(): CommandExecutor {
    val allplayer = Bukkit.getOnlinePlayers()
    override fun onCommand(sender: CommandSender?, p1: Command?, p2: String?, p3: Array<out String?>?): Boolean {
        allplayer.forEach { player ->
            player.saveData()
        }
        return true
    }
}