package numberSet.messages;

import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;

import java.util.*;

/**
 * Abstract Class {@code SetMessage} is used to deliver updates to peers in the network
 */
public abstract class SetMessage extends ProtoMessage {
    protected final List<Integer> numbers;

    public SetMessage(short uid, List<Integer> numbers) {
        super(uid);

        this.numbers = new ArrayList<>(numbers);
    }

    public int getSetSize() {
        return numbers.size();
    }

    public Iterator<Integer> getSetIterator() {
        return numbers.iterator();
    }
    public List<Integer> getNumbers() {
        return new ArrayList<>(numbers);
    }
}
