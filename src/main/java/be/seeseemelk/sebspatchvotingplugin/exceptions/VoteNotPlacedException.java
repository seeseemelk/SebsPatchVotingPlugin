package be.seeseemelk.sebspatchvotingplugin.exceptions;

public class VoteNotPlacedException extends IllegalArgumentException
{
	private static final long serialVersionUID = -6373659996414526986L;

	public VoteNotPlacedException()
	{
	}

	public VoteNotPlacedException(String s)
	{
		super(s);
	}

	public VoteNotPlacedException(Throwable cause)
	{
		super(cause);
	}

	public VoteNotPlacedException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
