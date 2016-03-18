/**
 * 
 */
var hoursPerDay = 7;

function standardDeviation(users) {
	var values=[];
	for(var i=0;i<users.length;i++){
		values.push(voteInHours(users[i]));
	}
	console.log(values);
	var avg = average(values);
	console.log(avg);
	var squareDiffs = values.map(function(value) {
		var diff = value - avg;
		var sqrDiff = diff * diff;
		return sqrDiff;
	});
	console.log(squareDiffs);
	var avgSquareDiff = average(squareDiffs);
	console.log(avgSquareDiff);
	var stdDev = Math.sqrt(avgSquareDiff);
	console.log('std '+stdDev);
	return stdDev;
}

function average(data) {
	var sum = data.reduce(function(sum, value) {
		return sum + value;
	}, 0);

	var avg = sum / data.length;
	return avg;
}

function voteInHours(user) {
	return user.voto * (user.tipoVoto == 0 ? 1 : hoursPerDay);
}