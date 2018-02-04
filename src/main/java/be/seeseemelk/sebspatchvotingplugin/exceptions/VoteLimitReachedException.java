package be.seeseemelk.sebspatchvotingplugin.exceptions;

public class VoteLimitReachedException extends IllegalArgumentException
{
	private static final long serialVersionUID = -3763774418166916379L;

	public VoteLimitReachedException()
	{
	}

	public VoteLimitReachedException(String s)
	{
		super(s);
	}

	public VoteLimitReachedException(Throwable cause)
	{
		super(cause);
	}

	public VoteLimitReachedException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
