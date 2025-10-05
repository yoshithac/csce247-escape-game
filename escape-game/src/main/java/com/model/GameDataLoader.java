package com.model;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GameDataLoader {
	private ClassLoader classLoader;
	protected static final String USER_FILE_NAME = "json\\Users.json";


		private ClassLoader getClassLoader() {
		if (classLoader == null) {
			classLoader = getClass().getClassLoader();
		}

		return classLoader;
	}

    /**
	 * Loads the list of system usernames and permissions.
	 *
	 * @param usersFile
	 *            the filename of the users file.
	 * @throws GRADSFileException
	 *             Exception.
	 */
	public void loadUsers() throws Exception {
		String usersFile = USER_FILE_NAME;
		try {
			// ClassLoader classLoader = getClass().getClassLoader();
			// File userFile = new
			// File(classLoader.getResource(usersFile).getFile());

			List<User> users = new Gson().fromJson(new FileReader(new File(getClassLoader().getResource(usersFile).getFile())),
					new TypeToken<List<User>>() {
					}.getType());

		} catch (Exception ex) {
			throw new Exception("File not found: Users file not found", ex);
		}
	}
}