package Tower;
 
import MVC.Point;

public class Tower6 extends Tower {

	public Tower6() {
		super();
		this.damage = 0;
		this.cost = 600;
		this.range = 0;
		this.exist = true;
		this.index = 6;
		this.name = "Tower 6";
	}

	public void upgrade() {
		this.damage *= 2;
	}

	// range ? // TODO
	public boolean inRange(Point point) {
		return false;
	};
}
