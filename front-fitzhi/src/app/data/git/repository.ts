/**
 * This class is used to retrieve the data sent by GitHub when we access the repository, 
 * such as _'https://api.github.com/repos/fitzhi/application'_
 * 
 * This is a very partial representation. Most of the properties are ignored.
 */
export class Repository {

    /**
     * Name of the repository
     */
    name: string;

    /**
     * Default branch
     */
    default_branch: string;

}