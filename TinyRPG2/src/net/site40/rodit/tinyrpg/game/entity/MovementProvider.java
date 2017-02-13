package net.site40.rodit.tinyrpg.game.entity;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.util.Direction;

public abstract class MovementProvider {

	public static final float SPEED_MULTI = 0.05f;

	public abstract void move(Game game, EntityPlayer entity);

	public static class InputMovementProvider extends MovementProvider{

		@Override
		public void move(Game game, EntityPlayer player){
			float deltaMulti = (float)game.getDelta() / 16.6f;
			Input input = game.getInput();
			if(input.allowMovement()){
				if(input.isDown(Input.KEY_UP))
					player.incVelocityY(-player.getStats().getMoveSpeed() * SPEED_MULTI * deltaMulti);
				if(input.isDown(Input.KEY_DOWN))
					player.incVelocityY(player.getStats().getMoveSpeed() * SPEED_MULTI * deltaMulti);
				if(input.isDown(Input.KEY_LEFT))
					player.incVelocityX(-player.getStats().getMoveSpeed() * SPEED_MULTI * deltaMulti);
				if(input.isDown(Input.KEY_RIGHT))
					player.incVelocityX(player.getStats().getMoveSpeed() * SPEED_MULTI * deltaMulti);
			}
		}
	}

	public static class TileMovementProvider extends MovementProvider{

		public static final long TILE_MOVE_DELAY = 20l;
		public static final int TILE_WIDTH = 16;
		public static final int TILE_HEIGHT = 16;

		private long lastTileLand = 0l;

		private Direction moveDir;
		private int moveTiles = 0;

		private long startTime;
		private float startX;
		private float startY;
		
		public void alignToTile(EntityPlayer player){
			if(moveTiles < 1 && (player.getBounds().getX() % (float)TILE_WIDTH != 0f || player.getBounds().getY() % (float)TILE_WIDTH != 0f)){
				player.setX(round(player.getBounds().getX(), 16));
				player.setY(round(player.getBounds().getY(), 16));
			}
		}
		
		public void setMovement(Game game, EntityPlayer player, Direction moveDir, int moveTiles){
			this.moveDir = moveDir;
			this.moveTiles = moveTiles;
			this.startX = player.getBounds().getX();
			this.startY = player.getBounds().getY();
			this.startTime = game.getTime();
			player.setMoveState(MovementState.WALK);
		}
		
		public int round(double i, int v){
		    return (int)(Math.round(i / v) * v);
		}

		@Override
		public void move(Game game, EntityPlayer player){
			if(game.getMap() == null || game.getMap().getMap() == null)
				return;
			Input input = game.getInput();
			if(moveTiles < 1 && game.getTime() - lastTileLand >= TILE_MOVE_DELAY){
				if(input.allowMovement() && !game.isShowingDialog()){
					boolean move = false;
					if(input.isDown(Input.KEY_UP)){
						moveDir = Direction.D_UP;
						move = true;
					}
					if(input.isDown(Input.KEY_DOWN)){
						moveDir = Direction.D_DOWN;
						move = true;
					}
					if(input.isDown(Input.KEY_LEFT)){
						moveDir = Direction.D_LEFT;
						move = true;
					}
					if(input.isDown(Input.KEY_RIGHT)){
						moveDir = Direction.D_RIGHT;
						move = true;
					}
					if(move){
						player.setDirection(moveDir);
						float newX = player.getBounds().getX();
						float newY = player.getBounds().getY();
						switch(moveDir){
						case D_UP:
							newY -= TILE_HEIGHT;
							break;
						case D_DOWN:
							newY += TILE_HEIGHT;
							break;
						case D_LEFT:
							newX -= TILE_WIDTH;
							break;
						case D_RIGHT:
							newX += TILE_WIDTH;
							break;
						}
						if(game.getMap().checkMove(game, player, newX, newY)){
							moveTiles = 1;
							startX = player.getBounds().getX();
							startY = player.getBounds().getY();
							startTime = game.getTime();
							player.setMoveState(MovementState.WALK);
						}
					}
				}
			}else if(input.allowMovement() && moveTiles > 0 && moveDir != null){
				long diff = game.getTime() - startTime;
				float totalTime = ((float)TILE_WIDTH / (2f * SPEED_MULTI));
				float fact = (float)diff / totalTime;
				float distance = Math.min(fact * (float)TILE_WIDTH, TILE_WIDTH);
				switch(moveDir){
				case D_UP:
					player.setY(startY - distance);
					player.setVelocityY(-Float.MIN_NORMAL);//-0.00001f);
					break;
				case D_DOWN:
					player.setY(startY + distance);
					player.setVelocityY(Float.MIN_NORMAL);//0.00001f);
					break;
				case D_LEFT:
					player.setX(startX - distance);
					player.setVelocityX(-Float.MIN_NORMAL);//-0.00001f);
					break;
				case D_RIGHT:
					player.setX(startX + distance);
					player.setVelocityX(Float.MIN_NORMAL);//0.00001f);
					break;
				}
				if(distance >= (float)TILE_WIDTH){
					moveDir = null;
					moveTiles--;
					lastTileLand = game.getTime();
					player.setMoveState(MovementState.IDLE);
					this.alignToTile(player);
					if(moveTiles > 0){
						startX = player.getBounds().getX();
						startY = player.getBounds().getY();
						startTime = game.getTime();
						player.setMoveState(MovementState.WALK);
					}
				}
			}
		}
	}
}
