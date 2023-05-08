if (!((line.substring(8)).contains(":"))) {
    messageArea.setForeground(Color.BLUE);
    messageArea.append(line.substring(8) + "\n");
} else if ((line.substring(8, line.indexOf(":"))).contains(username)) {
    messageArea.setForeground(Color.RED);
    //messageArea.append(line.substring(8, (line.indexOf(":")-1)));
    messageArea.setForeground(Color.BLACK);
    messageArea.append(line.substring(line.indexOf(":")) + "\n");
} else if(!((line.substring(8, line.indexOf(":"))).contains(username))) {
    messageArea.setForeground(Color.BLUE);
    //messageArea.append(line.substring(8, (line.indexOf(":")-1)));
    messageArea.setForeground(Color.BLACK);
    messageArea.append(line.substring(line.indexOf(":")) + "\n");
}
