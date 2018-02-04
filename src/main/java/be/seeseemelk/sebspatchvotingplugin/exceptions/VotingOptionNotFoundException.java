package be.seeseemelk.sebspatchvotingplugin.exceptions;

public class VotingOptionNotFoundException extends IllegalArgumentException
{
	private static final long serialVersionUID = 789083241487315586L;

	public VotingOptionNotFoundException()
	{
	}

	public VotingOptionNotFoundException(String s)
	{
		super(s);
	}

	public VotingOptionNotFoundException(Throwable cause)
	{
		super(cause);
	}

	public VotingOptionNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
