![Crawling](https://i.imgur.com/wghI3OE.png)

Crawling is a simple plugin that adds a **crawling** mechanic to minecraft. It allows you to enter **1 block tunnels** and hide in grass and other simillar blocks. It's a great addon for **RPG** and **Survival** servers.

![](https://i.imgur.com/Pwsgenu.png)
# Showcase
![gif](https://i.imgur.com/Tnr1LPd.gif)

![](https://i.imgur.com/Pwsgenu.png)
# Configuration
To prevent players from crawling in a worldguard region, you can use the command:
/rg flags region-name crawling deny

If you want players to need a permission to crawl or you want to make it impossible to crawl on a specific block, you can do that from the config!

<details>
  <summary><b>config.yml</b></summary>
  
  ```YAML
#Crawling by Arthed
#Support Server: https://discord.gg/MPKVEcX


############################################


#Crawling Modes:
#HOLD - players will continue crawling as long as they keep shift pressed
#TOGGLE - players will start crawling and get up when they press shift
#TUNNELS - players can enter one block tunnels by sneaking in front of them

#You can use multiple options at the same time
crawling_modes:
  - 'HOLD'
  - 'TOGGLE'
  - 'TUNNELS'

#How to start crawling for HOLD and TOGGLE modes:
#DOUBLE_SHIFT - double shift while looking down
#HOLD_X - hold shift for X seconds while looking down. Examples: 'HOLD_1', 'HOLD_0.5'

#You can use multiple options at the same time
start_crawling:
  - 'DOUBLE_SHIFT'

############################################


#List of blocks that players cant crawl on
blacklisted_blocks:
  - MAGMA_BLOCK

#blacklisted_blocks: [] - use this if you don't want any blocks to be blacklisted

#If true, players will only be able to crawl on blacklisted blocks
reverse_blocks_blacklist: false


############################################


#List of worlds in which players cant crawl
blacklisted_worlds:
  - example_world

#If true, players will only be able to crawl in blacklisted worlds
reverse_worlds_blacklist: false

#If you want to disable crawling just in a region, use the WorldGuard flag 'crawling'


############################################

#If true, players need the permission "crawling.player" to be able to crawl
need_permission_to_crawl: false
command_no_permission_message: '&cYou don''t have the permission to do that!'


############################################


ignore_updates: false
  ```
</details>


![](https://i.imgur.com/Pwsgenu.png)
# Statistics

<details>
  <summary><b>statistics</b></summary>
  <img src="https://bstats.org/signatures/bukkit/Crawling.svg">
</details>

![](https://i.imgur.com/Pwsgenu.png)

[![Discord](https://i.imgur.com/xGgAO7c.png)](https://discord.gg/MPKVEcX)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[![Spigot Page](https://i.imgur.com/idUVHgU.png)](https://www.spigotmc.org/resources/crawling.69126/)

# Compile
just use shadowJar
