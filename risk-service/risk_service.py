from flask import Flask, request, jsonify
app = Flask(__name__)

@app.route("/api/risk", methods=["POST"])
def score_risk():
    data = request.json
    return jsonify({
        "overallScore": 0.3,
        "identityRiskScore": 0.2,
        "behavioralRiskScore": 0.3,
        "transactionRiskScore": 0.4,
        "deviceRiskScore": 0.3,
        "geoLocationRiskScore": 0.4,
        "componentScores": {"device": 0.3, "geo": 0.4, "behavior": 0.3},
        "riskLevel": "LOW",
        "riskReason": "No suspicious patterns detected"
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8085)