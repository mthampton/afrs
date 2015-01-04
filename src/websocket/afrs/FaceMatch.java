package websocket.afrs;

class FaceMatch {
	private final int face;
	private final double confidence;

	FaceMatch(int face, double confidence) {
		this.face = face;
		this.confidence = confidence;
	}

	int getFace() {	return face; }

	double getConfidence() { return confidence;	}
}
