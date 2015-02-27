package trading.receiver;

import trading.domain.Stock;

public interface Receiver {
	boolean fetch(Stock stock); 
}
