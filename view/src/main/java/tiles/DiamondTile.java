package tiles;

import graphics.Assets;

public class DiamondTile extends Tile {

	public DiamondTile(int id) {
		super(Assets.diamond, id);
	}

	@Override
	public boolean isSolid(){
		return true;
	}
}