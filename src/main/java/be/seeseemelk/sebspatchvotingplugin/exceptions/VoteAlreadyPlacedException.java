package be.seeseemelk.sebspatchvotingplugin.exceptions;

public class VoteAlreadyPlacedException extends IllegalArgumentException
{
	private static final long serialVersionUID = 6929016950674394651L;

	public VoteAlreadyPlacedException()
	{
	}

	public VoteAlreadyPlacedException(String s)
	{
		super(s);
	}

	public VoteAlreadyPlacedException(Throwable cause)
	{
		super(cause);
	}

	public VoteAlreadyPlacedException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
