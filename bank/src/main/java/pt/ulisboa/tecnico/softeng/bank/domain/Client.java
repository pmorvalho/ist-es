package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Client extends Client_Base{
	private static int counter = 0;

	public Client(Bank bank, String name) {
		checkArguments(bank, name);

		setID(Integer.toString(++Client.counter));
		setName(name);

		bank.addClient(this);
	}

	private void checkArguments(Bank bank, String name) {
		if (bank == null || name == null || name.trim().equals("")) {
			throw new BankException();
		}
	}
	
	public int getCounter(){
		return counter;
	}

	public void delete() {
		for (Account account : this.getAccountSet()){
			account.delete();
		}
		setBank(null);
		deleteDomainObject();
	}

}
