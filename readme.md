## Introduction
MohammadHasan BakhtiariJavid
40413032

## How to run
java -jar ChickenInvaders.jar

## How to play:
| Key | Action |
|---|---|
| Arrow keys / W A S D | Move the plane (up / left / down / right) |
| Space | Shoot |
| P | Pause / resume the game |
| M | Open / close in-game settings overlay |
| Esc | End the current game and return (score and progress are saved) |

## Github repository
https://github.com/mbakhtiarijavid-hub/ChickenInvaders.git

## Database
type : sqlite
path : game.db
tables : 
	- `users` : one row per player account:
    	- `username` 
    	- `password` 
    	- `high_score` 
    	- `last_level` 
    	- `music_on`, `shot_on`, `crash_on`, `end_on`  — sound settings
    	- `selected_plane` — currently selected plane skin
	- `game_history` — one row per completed game session:
    	- `id` 
    	- `username` 
    	- `score` 
    	- `last_level` 
    	- `played_at` 
    	- `music_on`, `shot_on`, `crash_on`, `end_on` 


